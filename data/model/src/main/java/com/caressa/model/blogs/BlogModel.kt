package com.caressa.model.blogs

import com.google.gson.annotations.SerializedName

class BlogModel {

    data class BlogResponce(
        var blogList: List<Blog> = listOf()
    )

    data class Blog(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("date")
        val date: String = "",
        @SerializedName("date_gmt")
        val dateGmt: String = "",
        @SerializedName("guid")
        val guid: Guid = Guid(),
        @SerializedName("modified")
        val modified: String = "",
        @SerializedName("modified_gmt")
        val modifiedGmt: String = "",
        @SerializedName("slug")
        val slug: String = "",
        @SerializedName("status")
        val status: String = "",
        @SerializedName("type")
        val type: String = "",
        @SerializedName("link")
        val link: String = "",
        @SerializedName("title")
        val title: Title = Title(),
        @SerializedName("content")
        val content: Content = Content(),
        @SerializedName("excerpt")
        val excerpt: Excerpt = Excerpt(),
        @SerializedName("author")
        val author: Int = 0,
        @SerializedName("featured_media")
        val featuredMedia: Int = 0,
        @SerializedName("comment_status")
        val commentStatus: String = "",
        @SerializedName("ping_status")
        val pingStatus: String = "",
        @SerializedName("sticky")
        val sticky: Boolean = false,
        @SerializedName("template")
        val template: String = "",
        @SerializedName("format")
        val format: String = "",
        @SerializedName("meta")
        val meta: List<Any> = listOf(),
        @SerializedName("categories")
        val categories: List<Int> = listOf(),
        @SerializedName("tags")
        val tags: List<Any> = listOf(),
        @SerializedName("_links")
        val links: Links = Links()
    )

    data class Content(
        @SerializedName("protected")
        val `protected`: Boolean = false,
        @SerializedName("rendered")
        val rendered: String = ""
    )

    data class Excerpt(
        @SerializedName("protected")
        val `protected`: Boolean = false,
        @SerializedName("rendered")
        val rendered: String = ""
    )

    data class Guid(
        @SerializedName("rendered")
        val rendered: String = ""
    )

    data class Links(
        @SerializedName("about")
        val about: List<About> = listOf(),
        @SerializedName("author")
        val author: List<Author> = listOf(),
        @SerializedName("collection")
        val collection: List<Collection> = listOf(),
        @SerializedName("curies")
        val curies: List<Cury> = listOf(),
        @SerializedName("predecessor-version")
        val predecessorVersion: List<PredecessorVersion> = listOf(),
        @SerializedName("replies")
        val replies: List<Reply> = listOf(),
        @SerializedName("self")
        val self: List<Self> = listOf(),
        @SerializedName("version-history")
        val versionHistory: List<VersionHistory> = listOf(),
        @SerializedName("wp:attachment")
        val wpAttachment: List<WpAttachment> = listOf(),
        @SerializedName("wp:featuredmedia")
        val wpFeaturedmedia: List<WpFeaturedmedia> = listOf(),
        @SerializedName("wp:term")
        val wpTerm: List<WpTerm> = listOf()
    )

    data class About(
        @SerializedName("href")
        val href: String = ""
    )

    data class Author(
        @SerializedName("embeddable")
        val embeddable: Boolean = false,
        @SerializedName("href")
        val href: String = ""
    )

    data class Collection(
        @SerializedName("href")
        val href: String = ""
    )

    data class Cury(
        @SerializedName("href")
        val href: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("templated")
        val templated: Boolean = false
    )

    data class PredecessorVersion(
        @SerializedName("href")
        val href: String = "",
        @SerializedName("id")
        val id: Int = 0
    )

    data class Reply(
        @SerializedName("embeddable")
        val embeddable: Boolean = false,
        @SerializedName("href")
        val href: String = ""
    )

    data class Self(
        @SerializedName("href")
        val href: String = ""
    )

    data class VersionHistory(
        @SerializedName("count")
        val count: Int = 0,
        @SerializedName("href")
        val href: String = ""
    )

    data class WpAttachment(
        @SerializedName("href")
        val href: String = ""
    )

    data class WpFeaturedmedia(
        @SerializedName("embeddable")
        val embeddable: Boolean = false,
        @SerializedName("href")
        val href: String = ""
    )

    data class WpTerm(
        @SerializedName("embeddable")
        val embeddable: Boolean = false,
        @SerializedName("href")
        val href: String = "",
        @SerializedName("taxonomy")
        val taxonomy: String = ""
    )

    data class Title(
        @SerializedName("rendered")
        val rendered: String = ""
    )


}