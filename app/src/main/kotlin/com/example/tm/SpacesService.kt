package com.example.tm

import retrofit2.Call
import retrofit2.http.GET

interface SpacesService {
    @GET("spaces/pxqrocxwsjcc/entries?content_type=video&sys.id=tmk-video-e08fb7616f8a4a63936e9919c177ffce&limit=1&access_token=0298f30529804286813d5825feaf2890f24e0756e31c41a78b971e1d7d56c251")
    fun entries(): Call<Entries>
}