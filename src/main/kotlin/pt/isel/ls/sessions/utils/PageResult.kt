package pt.isel.ls.sessions.utils

data class PageResult<T>(
    val content: List<T>,
    val nextPage: Int?,
    val previousPage: Int?,
    val firstPage: Int?,
    val lastPage: Int?,
    val pageSize: Int,
) {
    companion object {
        fun <T> toPage(
            objects: Collection<T>,
            skip: Int,
            limit: Int,
        ): PageResult<T> {
            val from = skip
            var content: List<T> = emptyList()
            if (from < objects.size) {
                var to = Math.min(from + limit, objects.size)
                to = if (to > objects.size) objects.size else to
                content = ArrayList(objects).subList(from, to)
            }
            val l = (objects.size - 1) / limit + 1
            val lastPage = if (objects.isNotEmpty() && skip / limit + 1 < l) l else null
            val nextPage = if (lastPage != null && skip / limit + 1 < lastPage) skip / limit + 2 else null
            val previousPage = if (skip / limit + 1 > 1) skip / limit else null
            val firstPage = if (skip / limit + 1 >= 1) 1 else null
            return PageResult(
                content,
                nextPage,
                previousPage,
                firstPage,
                lastPage,
                limit,
            )
        }
    }
}
