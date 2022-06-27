package com.regis.quarkussocial.dto;

import com.regis.quarkussocial.domain.model.Follower;
import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {

    private Integer followersCount;
    private List<FollowerResponse> content;

}
