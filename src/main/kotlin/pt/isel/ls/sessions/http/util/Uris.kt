package pt.isel.ls.sessions.http.util

object Uris {
    const val DEFAULT = ""

    object Players {
        const val ROOT = "player"
        const val BY_ID = "/{pid}"
    }

    object Games {
        const val ROOT = "game"
        const val BY_ID = "/{gid}"
    }

    object Sessions {
        const val ROOT = "session"
        const val GET_BY_ID = "/{sid}"
        const val ADD_PLAYER = "/{sid}/player/{pid}"
    }
}
