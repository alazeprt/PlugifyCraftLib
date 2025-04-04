package top.alazeprt.pclib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JsonUtil {
    public static JsonArray sortByReleaseDate(JsonArray originalArray) {
        // 创建可修改的List
        List<JsonObject> jsonObjects = new ArrayList<>();

        // 转换所有元素为JsonObject
        for (JsonElement element : originalArray) {
            jsonObjects.add(element.getAsJsonObject());
        }

        // 自定义降序比较器
        jsonObjects.sort((o1, o2) -> {
            int date1 = o1.get("releaseDate").getAsInt();
            int date2 = o2.get("releaseDate").getAsInt();
            // 降序排列（大的在前）
            return Integer.compare(date2, date1);
        });

        // 构建新的JsonArray
        JsonArray sortedArray = new JsonArray();
        for (JsonObject obj : jsonObjects) {
            sortedArray.add(obj);
        }

        return sortedArray;
    }
}
