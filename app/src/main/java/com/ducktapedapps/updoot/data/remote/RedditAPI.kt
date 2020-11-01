package com.ducktapedapps.updoot.data.remote

import com.ducktapedapps.updoot.data.local.model.*
import com.ducktapedapps.updoot.data.local.moshiAdapters.InconsistentApiResponse
import com.ducktapedapps.updoot.utils.Constants
import retrofit2.http.*

interface RedditAPI {

    @GET("/api/v1/me")
    suspend fun userIdentity(): Account

    @GET("{subreddit}/{sort}")
    suspend fun getSubreddit(
            @Path("subreddit") subreddit: String?,
            @Path("sort") sort: String,
            @Query("t") time: String?,
            @Query("after") after: String?): Listing<LinkData>

    @FormUrlEncoded
    @POST("/api/save")
    suspend fun save(
            @Field("id") id: String
    ): retrofit2.Response<Unit>

    @FormUrlEncoded
    @POST("/api/unsave")
    suspend fun unSave(
            @Field("id") id: String
    ): retrofit2.Response<Unit>

    @GET("r/{subreddit}/comments/{id}")
    suspend fun getComments(
            @Path("subreddit") subreddit: String,
            @Path("id") submissions_id: String
    ): List<Listing<RedditThing>>

    @GET("/api/morechildren")
    @InconsistentApiResponse
    suspend fun getMoreChildren(
            @Query("api_type") api_type: String = "json",
            @Query("link_id") link_id: String,
            @Query("children") children: String
    ): Listing<Comment>

    @FormUrlEncoded
    @POST("/api/vote")
    suspend fun castVote(
            @Field("id") thing_id: String,
            @Field("dir") vote_direction: Int
    ): retrofit2.Response<Unit>

    @GET("/subreddits/search")
    suspend fun search(
            @Query("q") query: String
    ): Listing<Subreddit>

    @GET("r/{subreddit}/about")
    suspend fun getSubredditInfo(
            @Path("subreddit") subreddit: String
    ): Subreddit

    @GET
    suspend fun getTrendingSubredditNames(@Url fullUrl: String = Constants.TRENDING_API_URL): TrendingSubredditNames

    @GET("/subreddits/mine/subscriber")
    suspend fun getSubscribedSubreddits(
            @Query("after") after: String?
    ): Listing<Subreddit>
}
