package com.dpdocter.services.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Blog;
import com.dpdocter.beans.BlogImageUploadReqest;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.collections.BlogCollection;
import com.dpdocter.collections.BlogImagesCollection;
import com.dpdocter.collections.DentalChainLandmarkLocalityCollection;
import com.dpdocter.collections.TagCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.enums.BlogSuperCategoryType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BlogImagesRepository;
import com.dpdocter.repository.BlogRepository;
import com.dpdocter.repository.GridFsRepository;
import com.dpdocter.repository.TagRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.BlogResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.TagResponse;
import com.dpdocter.services.BlogService;
import com.dpdocter.services.FileManager;
import com.mongodb.client.gridfs.model.GridFSFile;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class BlogServiceImpl implements BlogService {
	private static Logger logger = LogManager.getLogger(BlogServiceImpl.class.getName());

	@Autowired
	private GridFsRepository gridfsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private BlogImagesRepository blogImageRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	GridFsTemplate gridFsTemplate;

	@Override
	public String saveBlogArticle(String article) {
		try {
			InputStream inputStream = IOUtils.toInputStream(article);
			ObjectId file = gridfsRepository.save(inputStream, "Blog" + new Date());
			return file.toString();
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}

	@Override
	public String getBlogArticle(String id) {

		try {
			GridFSFile file = gridfsRepository.read(new ObjectId(id));
			GridFsResource resource = gridFsTemplate.getResource(file);
			InputStream inputStream = resource.getInputStream();

			return IOUtils.toString(inputStream);
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public List<String> uploadBlogImages(BlogImageUploadReqest request) {
		List<String> blogImageUrl = null;
		UserCollection userCollection;
		try {
			blogImageUrl = new ArrayList<String>();

			userCollection = userRepository.findById(new ObjectId(request.getUserId())).orElse(null);
			if (userCollection != null && userCollection.getUserState().getState().equals("ADMIN")) {

				for (FileDetails image : request.getImages()) {
					image.setFileName(image.getFileName() + new Date().getTime());
					String path = "blog" + File.separator + "images";
					BlogImagesCollection blogImagesCollection = new BlogImagesCollection();
					blogImagesCollection.setCatagory(request.getCatagory());
					blogImagesCollection.setImageName(image.getFileName() + "." + image.getFileExtension());
					blogImagesCollection.setImagePath(path);
					blogImagesCollection.setUserId(userCollection.getId());
					blogImagesCollection.setCreatedBy(userCollection.getFirstName());
					blogImagesCollection.setCreatedTime(new Date());
					blogImageRepository.save(blogImagesCollection);

					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(image, path, false);
					blogImageUrl.add(imagePath + imageURLResponse.getImageUrl());
				}

			} else {
				throw new BusinessException(ServiceError.Unknown, "Error Invalid Admin : ");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return blogImageUrl;

	}

	@Override
	public Blog saveBlog(Blog request) {
		Blog response = null;

		try {
			BlogCollection blogCollection = new BlogCollection();

			UserCollection userCollection = userRepository.findById(new ObjectId(request.getUserId())).orElse(null);
			if (userCollection != null && userCollection.getUserState().getState().equals("ADMIN")) {

				BeanUtil.map(request, blogCollection);
				if (DPDoctorUtils.anyStringEmpty(request.getId())) {
					blogCollection.setCreatedBy(userCollection.getFirstName());

					if (DPDoctorUtils.anyStringEmpty(request.getPostBy()))
						blogCollection.setPostBy(userCollection.getFirstName());
					if (request.getTitleImage() != null) {
						String image = request.getTitleImage();
						image = image.replaceAll(imagePath, "");
						blogCollection.setTitleImage(image);
					}
					blogCollection.setCreatedTime(new Date());

				} else {
					BlogCollection oldblogCollection = blogRepository.findById(blogCollection.getId()).orElse(null);
					oldblogCollection.setBlogParagraph(null);
					if (request.getTitleImage() != null) {
						String image = request.getTitleImage();
						image = image.replaceAll(imagePath, "");
						blogCollection.setTitleImage(image);
					} else
						blogCollection.setTitleImage(oldblogCollection.getTitleImage());

					blogCollection.setCreatedBy(oldblogCollection.getCreatedBy());
					blogCollection.setCreatedTime(oldblogCollection.getCreatedTime());
					blogCollection.setNoOfLikes(oldblogCollection.getNoOfLikes());
					blogCollection.setViews(oldblogCollection.getViews());

				}
				if (!DPDoctorUtils.anyStringEmpty(request.getArticle())) {
					if (!DPDoctorUtils.anyStringEmpty(request.getArticleId())) {
						gridfsRepository.delete(blogCollection.getArticleId());
					}
					String id = this.saveBlogArticle(request.getArticle());
					blogCollection.setArticleiId(new ObjectId(id));

				} else if (!request.getIsSmilebirdBlog()) {
					throw new BusinessException(ServiceError.Unknown, "Error Blank Article  : ");
				}
				blogCollection = blogRepository.save(blogCollection);
				response = new Blog();
				BeanUtil.map(blogCollection, response);
				response.setArticle(request.getArticle());
				if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
					response.setTitleImage(imagePath + response.getTitleImage());

			} else
				throw new BusinessException(ServiceError.Unknown, "Error Admin Not Found : ");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	@Override
	public BlogResponse getBlogs(int size, int page, String category, String userId, String title, Boolean discarded,
			Boolean isSmilebirdBlog) {
		BlogResponse response = new BlogResponse();
		List<Blog> blogList = null;
		try {
			blogList = new ArrayList<Blog>();
			Criteria criteria = new Criteria().and("discarded").is(discarded);

			criteria.and("isSmilebirdBlog").is(isSmilebirdBlog);

			Aggregation aggregation = null;

			List<BlogCollection> blogCollections = null;
			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria = criteria.and(userId).is(new ObjectId(userId));

			if (!DPDoctorUtils.anyStringEmpty(title))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + title + ".*", "i"));
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria = criteria.and("category").is(category);
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.match(criteria), Aggregation.skip((long) (page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.match(criteria));
			}
			blogCollections = mongoTemplate.aggregate(aggregation, BlogCollection.class, BlogCollection.class)
					.getMappedResults();
			blogList = new ArrayList<Blog>();
			for (BlogCollection blogCollection : blogCollections) {
				Blog blog = new Blog();
				BeanUtil.map(blogCollection, blog);
				blog.setArticle(this.getBlogArticle(blog.getArticleId()));
				if (!DPDoctorUtils.anyStringEmpty(blog.getTitleImage()))
					blog.setTitleImage(imagePath + blog.getTitleImage());

				blogList.add(blog);
				response.setBlogs(blogList);
				response.setTotalsize((int) mongoTemplate.count(new Query(criteria), BlogCollection.class));
				// if (blogList != null && !blogList.isEmpty()) {
				// response.setTotalsize(blogList.size());
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	@Override
	public Blog getBlog(String id, String slugURL) {
		Blog response = null;
		try {
			BlogCollection blogCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(id))
				blogCollection = blogRepository.findById(new ObjectId(id)).orElse(null);
			else
				blogCollection = blogRepository.findBySlugURL(slugURL);
			response = new Blog();
			BeanUtil.map(blogCollection, response);
			response.setArticle(this.getBlogArticle(response.getArticleId()));
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
				response.setTitleImage(imagePath + response.getTitleImage());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	@Override
	public Blog deleteBlog(String blogId, String userId) {
		Blog response = null;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null) {
				BlogCollection blogCollection = blogRepository.findById(new ObjectId(blogId)).orElse(null);
				blogCollection.setDiscarded(!blogCollection.getDiscarded());
				blogCollection = blogRepository.save(blogCollection);
				response = new Blog();
				BeanUtil.map(blogCollection, response);
				if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
					response.setTitleImage(imagePath + response.getTitleImage());

			} else
				throw new BusinessException(ServiceError.Unknown, "Error Admin Not Found : ");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

//	@Override
//	public List<String> getBlogImagesUrl(int size, int page, String category, String userId) {
//		List<String> response = null;
//		List<BlogImagesCollection> blogImagesCollections;
//		try {
//			if (size > 0) {
//				if (!DPDoctorUtils.anyStringEmpty(userId) && !DPDoctorUtils.anyStringEmpty(category))
//					blogImagesCollections = blogImageRepository.findByUserIdAndCatagory(new ObjectId(userId),
//							category,
//							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
//				else if (!DPDoctorUtils.anyStringEmpty(userId))
//					blogImagesCollections = blogImageRepository.findByUserId(new ObjectId(userId),
//							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
//				else if (!DPDoctorUtils.anyStringEmpty(category))
//					blogImagesCollections = blogImageRepository
//							.findByCatagory(category, PageRequest.of(page, size, Direction.DESC, "updatedTime"));
//				else
//					blogImagesCollections = blogImageRepository
//							.findAll(PageRequest.of(page, size, Direction.DESC, "updatedTime")).getContent();
//			} else {
//				if (!DPDoctorUtils.anyStringEmpty(userId) && !DPDoctorUtils.anyStringEmpty(category))
//					blogImagesCollections = blogImageRepository.findByUserIdAndCatagory(new ObjectId(userId), category);
//				else if (!DPDoctorUtils.anyStringEmpty(userId))
//					blogImagesCollections = blogImageRepository.findByUserId(new ObjectId(userId));
//				else if (!DPDoctorUtils.anyStringEmpty(category))
//					blogImagesCollections = blogImageRepository.findByCatagory(category);
//				else
//					blogImagesCollections = blogImageRepository.findAll();
//			}
//			response = new ArrayList<String>();
//			for (BlogImagesCollection blogImagesCollection : blogImagesCollections) {
//
//				String ImageUrl = imagePath + blogImagesCollection.getImagePath() + "/"
//						+ blogImagesCollection.getImageName();
//				response.add(ImageUrl);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e);
//			throw new BusinessException(ServiceError.Unknown, e.getMessage());
//
//		}
//		return response;
//	}

	@Override
	public List<String> getBlogImagesUrl(int size, int page, String category, String userId) {
		List<String> response = null;
		List<BlogImagesCollection> blogImagesCollections = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria.and("userId").is(new ObjectId(userId));

			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria.and("category").is(category);

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			blogImagesCollections = mongoTemplate
					.aggregate(aggregation, BlogImagesCollection.class, BlogImagesCollection.class).getMappedResults();

			response = new ArrayList<String>();
			for (BlogImagesCollection blogImagesCollection : blogImagesCollections) {

				String ImageUrl = imagePath + blogImagesCollection.getImagePath() + "/"
						+ blogImagesCollection.getImageName();
				response.add(ImageUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	public BlogCategoryType[] getBlogCategory() {

		return BlogCategoryType.values();

	}

	@Override
	public Boolean updateBlogSuperCategory(String blogId, String userId, String category) {
		Boolean response = false;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null) {
				BlogCollection blogCollection = blogRepository.findById(new ObjectId(blogId)).orElse(null);
				if (blogCollection != null) {
					blogCollection.setSuperCategory(BlogSuperCategoryType.valueOf(category));
					blogCollection.setUpdatedTime(new Date());
					blogCollection = blogRepository.save(blogCollection);
				}
			} else
				throw new BusinessException(ServiceError.Unknown, "Error Admin Not Found : ");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public TagResponse saveTag(TagResponse request) {
		TagResponse response = null;
		try {
			TagCollection tagCollection = new TagCollection();

			BeanUtil.map(request, tagCollection);
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				tagCollection.setCreatedTime(new Date());
			} else {
				tagCollection = tagRepository.findById(tagCollection.getId()).orElse(null);
				tagCollection.setCreatedTime(tagCollection.getCreatedTime());
				tagCollection.setUpdatedTime(new Date());
			}
			tagCollection = tagRepository.save(tagCollection);
			response = new TagResponse();
			BeanUtil.map(tagCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean deleteTag(String tagId) {
		Boolean response = false;
		try {
			TagCollection tagCollection = tagRepository.findById(new ObjectId(tagId)).orElse(null);
			if (tagCollection != null) {
				tagCollection.setDiscarded(true);
				tagCollection.setUpdatedTime(new Date());
				tagCollection = tagRepository.save(tagCollection);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getTags(int size, int page, Boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (discarded == false)
				criteria.and("discarded").ne(true);
			else
				criteria.and("discarded").is(discarded);

			Aggregation aggregation = null;

			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					TagCollection.class, TagCollection.class).getMappedResults().size());

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			}
			AggregationResults<TagResponse> groupResults = mongoTemplate.aggregate(aggregation, TagCollection.class,
					TagResponse.class);
			response.setDataList(groupResults.getMappedResults());

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}
