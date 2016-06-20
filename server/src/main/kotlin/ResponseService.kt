import io.vertx.core.Future

/**
 * Created by 4 on 09.06.2016.
 */
data class User(val email: String,
                val pass: String,
                val date: String,
                var ip: String,
                var countInput: String,
                var status: String) {
    override fun toString(): String {
        return "$email,$pass,$date,$ip,$countInput,$status"
    }
}

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

    fun getUser(user: User): Future<String> {
        return Future.succeededFuture(User(
                user.email, user.pass,
                user.date, user.ip, user.countInput, user.status).toString())
    }

    fun getUser(email: String, pass: String, date: String, ip: String, countInput: String, status: String): Future<String> {
        return Future.succeededFuture(User(email, pass, date, ip, countInput, status).toString())
    }
}