package com.regis.quarkussocial.rest;

import com.regis.quarkussocial.domain.model.Follower;
import com.regis.quarkussocial.domain.model.User;
import com.regis.quarkussocial.domain.repository.FollowerRepository;
import com.regis.quarkussocial.domain.repository.UserRepository;
import com.regis.quarkussocial.dto.FollowerRequest;
import com.regis.quarkussocial.dto.FollowerResponse;
import com.regis.quarkussocial.dto.FollowersPerUserResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository repository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {
        if (userId.equals(request.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(request.getFollowerId());

        boolean follows = repository.followers(follower, user);

        if (!follows) {
            Follower entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            repository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = repository.findByUser(userId);

        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());

        var followerList = list.stream().map(f -> new FollowerResponse(f)).collect(Collectors.toList());
        responseObject.setContent(followerList);

        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
