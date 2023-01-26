package model.service;

import java.util.Map;
import java.util.Optional;

class ModelServiceUtils {
  static <K, V> V getValue(Map<K, V> map, K key) {
    return Optional.ofNullable(map.get(key))
      .orElseThrow(() -> new RuntimeException("key " + key + " does not exist"));
  }

  static <K, V> K getKey(Map<K, V> map, V value) {
    for (Map.Entry<K, V> entry : map.entrySet()) {
      if (entry.getValue().equals(value)) {
        return entry.getKey();
      }
    }
    throw new RuntimeException("value " + value + " does not exist");
  }
}
