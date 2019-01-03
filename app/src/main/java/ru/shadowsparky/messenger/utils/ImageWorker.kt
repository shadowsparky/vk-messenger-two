package ru.shadowsparky.messenger.utils

import ru.shadowsparky.messenger.response_utils.pojos.VKPhotoSize
import java.util.ArrayList

class ImageWorker {

    fun getHashmapCard(list: ArrayList<VKPhotoSize>) : HashMap<String, String> {
        val hashmap = HashMap<String, String>()
        for (element in list)
            hashmap[element.type] = element.url
        return hashmap
    }

    fun getOptimalImage(map: HashMap<String, String>) : String? = when {
        map["w"] != null -> map["w"]!!
        map["z"] != null -> map["z"]!!
        map["y"] != null -> map["y"]!!
        map["x"] != null -> map["x"]!!
        map["m"] != null -> map["m"]!!
        map["s"] != null -> map["s"]!!
        else -> null
    }
}