package com.adityavikas.codeverse.dto;

import lombok.Data;
import org.bson.types.ObjectId;


public record EditorAccessDTO (
    ObjectId userId
){}
