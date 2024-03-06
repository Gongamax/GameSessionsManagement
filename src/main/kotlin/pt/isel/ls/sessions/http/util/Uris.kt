package pt.isel.ls.sessions.http.util

object Uris {
    object Players {
        const val ROOT = "player"
        const val BY_ID = "player/{pid}"
    }

    object Games {
        const val ROOT = "game"
        const val BY_ID = "game/{gid}"
    }

    object Sessions {
        const val ROOT = "session"
        const val GET_BY_ID = "session/{sid}"
        const val CREATE = "session"
        const val ADD_PLAYER = "session/{sid}/player/{pid}"
    }
}