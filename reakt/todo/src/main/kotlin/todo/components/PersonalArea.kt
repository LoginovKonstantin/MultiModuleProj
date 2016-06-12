package todo.components

import com.github.andrewoma.react.*

/**
 * Created by 4 on 11.06.2016.
 */
data class UserProps(
        val email: String,
        val pass: String,
        val date: String,
        var ip: String,
        var countInput: String
)

class PersonalArea : ComponentSpec<UserProps, Unit>() {

    companion object {
        val factory = react.createFactory(PersonalArea())

    }

    override fun Component.render() {
        div {
            h3 {
                span ({className = "label label-success"}){
                    text("Добро пожаловать, ${props.email}");
                }
            }

        }
    }
}
fun createPersonalArea(userProps: UserProps)
        = PersonalArea.factory(Ref(userProps))