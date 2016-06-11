package todo

import com.github.andrewoma.react.react
import createLogin
import kotlin.browser.document

fun main(args: Array<String>) {
    react.render(createLogin(), document.getElementById("app")!!)
}