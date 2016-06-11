import io.vertx.core.Future

/**
 * Created by 4 on 09.06.2016.
 */

class ResponseService {

    fun registrationSuccess(): Future<String> {
        return Future.succeededFuture("registrationSuccess");
    }

    fun registrationFail(): Future<String> {
        return Future.succeededFuture("registrationFail");
    }
}