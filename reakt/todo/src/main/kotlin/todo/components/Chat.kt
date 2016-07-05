package todo.components

import com.github.andrewoma.react.*

/**
 * Created by 4 on 05.07.2016.
 */
class Chat : ComponentSpec<String, Unit>() {

    companion object {
        val factory = react.createFactory(Chat())
    }

    override fun Component.render() {
        div({className = "span9"}) {
            div ({className = "hero-unit"}){
                h1 { text("Чат: $props") }
            }
            text(props)
            button ({className = "btn btn-large btn-success"}){text("button")  }
        }
    }
}

fun createChat(nameConcreteChat: String)
        = Chat.factory(Ref(nameConcreteChat))
