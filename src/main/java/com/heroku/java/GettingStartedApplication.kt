package com.heroku.java

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

import java.sql.Connection
import java.sql.SQLException
import java.util.ArrayList
import java.util.Random
import javax.sql.DataSource

@SpringBootApplication
@Controller
class GettingStartedApplication @Autowired constructor(
    private val dataSource: DataSource
) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/database")
    @Throws(SQLException::class)
    fun database(model: MutableMap<String, Any>): String {
        println("Rhett Edwards")

        dataSource.connection.use { connection ->
            val statement = connection.createStatement()

            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS table_timestamp_and_random_string " +
                        "(tick timestamp, random_string varchar(50))"
            )

            statement.executeUpdate(
                "INSERT INTO table_timestamp_and_random_string VALUES " +
                        "(now(), '${getRandomString()}')"
            )

            val resultSet = statement.executeQuery(
                "SELECT tick, random_string FROM table_timestamp_and_random_string"
            )

            val output = ArrayList<String>()

            while (resultSet.next()) {
                output.add(
                    "Read from DB: " +
                            resultSet.getTimestamp("tick") +
                            " | Random String: " +
                            resultSet.getString("random_string")
                )
            }

            model["records"] = output
            return "database"
        }
    }

    companion object {

        fun getRandomString(): String {
            val characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

            val random = Random()
            val length = random.nextInt(6) + 5 // 5-10 characters

            val result = StringBuilder(length)

            for (i in 0 until length) {
                result.append(
                    characters[random.nextInt(characters.length)]
                )
            }

            return result.toString()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GettingStartedApplication::class.java, *args)
        }
    }
}