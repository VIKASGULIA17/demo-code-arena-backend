package com.vikas.demo.repository;

import com.vikas.demo.entity.UserProfile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, ObjectId> {
    UserProfile findByUserName(String userName);
}
