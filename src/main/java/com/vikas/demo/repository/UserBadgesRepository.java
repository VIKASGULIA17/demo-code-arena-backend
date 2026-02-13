package com.vikas.demo.repository;

import com.vikas.demo.entity.UserBadges;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserBadgesRepository extends MongoRepository<UserBadges,String> {
//    UserBadges findByUserBadges(String name);
}
