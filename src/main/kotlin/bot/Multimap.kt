package bot

import com.google.common.collect.ImmutableListMultimap

fun <K, V> from(entries: Iterable<Pair<K, V>>): ImmutableListMultimap<K, V>? =
    ImmutableListMultimap.builder<K, V>().let { map ->
        entries.forEach { map.put(it.first, it.second) }
        return map.build()
}