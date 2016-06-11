import io.vertx.core.Future

/**
 * Created by 4 on 09.06.2016.
 */
data class User(val email: String, val pass: String, val date: String, val ip: String, val countInput: String)

class ResponseService {

    fun registrationSuccess(): Future<String> {
        return Future.succeededFuture("registrationSuccess");
    }

    fun registrationFail(): Future<String> {
        return Future.succeededFuture("registrationFail");
    }

    fun loginSuccess(): Future<String> {
        return Future.succeededFuture("loginSuccess")
    }

    fun loginFail(): Future<String> {
        return Future.succeededFuture("loginFail")
    }

    fun getUser(email: String, pass: String, date: String, ip: String, countInput: String): Future<User> {
        return Future.succeededFuture(User(email, pass, date, ip, countInput))
    }
}