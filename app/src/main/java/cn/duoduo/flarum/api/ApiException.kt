package cn.duoduo.flarum.api

class ApiException(message: String): Exception(message) {

    companion object {
        @JvmStatic
        fun newException(errors: List<cn.duoduo.flarum.api.models.Error>): ApiException {
            val sb = StringBuilder()
            for (error in errors) {
                sb.append(error.status).append(" - ").append(error.code)
            }
            return ApiException(sb.toString())
        }
    }

}