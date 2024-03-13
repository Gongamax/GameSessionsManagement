package pt.isel.ls.sessions.http.util

object Uris {
    object Players {
        const val ROOT = "player"
        const val BY_ID = "/{pid}"
    }

    object Games {
        const val ROOT = "game"
        const val BY_ID = "/{gid}"
        const val GAMES = "/games"
    }

    object Sessions {
        const val ROOT = "session"
        const val GET_BY_ID = "/{sid}"
        const val CREATE = "/create"
        const val ADD_PLAYER = "/{sid}/player/{pid}"
    }
}