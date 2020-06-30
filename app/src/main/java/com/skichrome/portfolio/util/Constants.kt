package com.skichrome.portfolio.util

// Cloud Firestore and Storage

const val ROOT_COLLECTION = "portfolio"
const val CURRENT_VERSION = "V1.0"
const val USER_COLLECTION = "user_infos"
const val THEMES_COLLECTION = "themes"
const val CATEGORIES_COLLECTION = "categories"
const val PROJECTS_COLLECTION = "projects"

// Camera

const val PICTURES_USERS_FOLDER_NAME = "users_media"
const val PICTURES_THEME_FOLDER_NAME = "themes_media"
const val PICTURES_CATEGORY_FOLDER_NAME = "categories_media"
const val PICTURES_PROJECT_FOLDER_NAME = "projects_media"
const val RC_IMAGE_CAPTURE_USER_INTENT = 453
const val RC_IMAGE_CAPTURE_THEME_INTENT = 454
const val RC_IMAGE_CAPTURE_CATEGORY_INTENT = 455
const val RC_IMAGE_CAPTURE_PROJECTS_INTENT = 456
const val RC_IMAGE_CAPTURE_PARAGRAPHS_INTENT = 457

const val CURRENT_USER_PICTURE_PATH_REF = "user_picture_state_ref"
const val CURRENT_REMOTE_USER_PICTURE_PATH_REF = "user_remote_picture_state_ref"
const val CURRENT_THEME_PICTURE_PATH_REF = "theme_picture_state_ref"
const val CURRENT_REMOTE_THEME_PICTURE_PATH_REF = "theme_remote_picture_state_ref"
const val CURRENT_CATEGORY_PICTURE_PATH_REF = "category_picture_state_ref"
const val CURRENT_REMOTE_CATEGORY_PICTURE_PATH_REF = "category_remote_picture_state_ref"
const val CURRENT_PROJECT_PICTURE_PATH_REF = "project_picture_state_ref"
const val CURRENT_REMOTE_PROJECT_PICTURE_PATH_REF = "project_remote_picture_state_ref"