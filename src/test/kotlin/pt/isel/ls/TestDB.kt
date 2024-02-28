package pt.isel.ls

import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.*

class TestDB {

    private val dataSource = PGSimpleDataSource()

    @BeforeTest
    fun setup() {
        val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        dataSource.setURL(jdbcDatabaseURL)
    }

    @Test
    fun testDB() {
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from students")
            val rs = stm.executeQuery()
            while (rs.next()) {
                println(rs.getString("name"))
            }
        }
    }

    @Test
    fun `test basic insert and delete`() {
        dataSource.connection.use {
            val stdNumber = (1000..9999).random()
            val stm = it.prepareStatement("insert into students values ($stdNumber, 'Joao', 1)")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)

            // Clean up
            val stm2 = it.prepareStatement("delete from students where number = $stdNumber")
            val rs2 = stm2.executeUpdate()
            assertEquals(1, rs2)
        }
    }

    @Test
    fun `complete test for insertion`() {
        val stdNumber = getRandomNumber()
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into students values ($stdNumber, 'Joao', 1)")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from students where number = $stdNumber")
            val rs = stm.executeQuery()
            assertTrue(rs.next())

            // Clean up
            val stm2 = it.prepareStatement("delete from students where number = $stdNumber")
            val rs2 = stm2.executeUpdate()
            assertEquals(1, rs2)
        }
    }

    @Test
    fun `insert students test`(){
        val number = getRandomNumber()
        val name = "Maria"

        dataSource.connection.use {
            val stm = it.prepareStatement("insert into students values ($number,'$name',1)")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
        dataSource.connection.use{
            val stm = it.prepareStatement("select * from students where number = $number")
            val rs = stm.executeQuery()
            assert(rs.next())
        }
        dataSource.connection.use {
            val stm = it.prepareStatement("delete from students where number = $number")
            val rs = stm.executeUpdate()
            assertEquals(1,rs)
        }
    }

    @Test
    fun `Insert new course and new student and update name by the number and course of the student`() {
        val studentNumber = getRandomNumber()

        // Insert new course
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into courses(name) values ('$dummyCourseName')")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }

        var cid = 0
        // Verify if the course was inserted
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from courses where name = '$dummyCourseName'")
            val rs = stm.executeQuery()
            assert(rs.next())
            cid = rs.getInt("cid")
        }

        // Insert new student
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into students values ($studentNumber,'$dummyStudentName', $cid)")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }

        // Verify if the student was inserted
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from students where number = $studentNumber")
            val rs = stm.executeQuery()
            assert(rs.next())
        }

        // Update the student name
        dataSource.connection.use {
            val stm = it.prepareStatement("update students set name = '$dummyStudentNewName' where number = $studentNumber and course = $cid")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }

        // Verify if the student name was updated
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from students where number = $studentNumber")
            val rs = stm.executeQuery()
            assert(rs.next())
        }

        // Clean up
        dataSource.connection.use {
            val stm = it.prepareStatement("delete from students where number = $studentNumber")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
        dataSource.connection.use {
            val stm = it.prepareStatement("delete from courses where name = '$dummyCourseName'")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
    }

    companion object {
        private fun getRandomNumber() = (1000..9999).random()
        private val dummyStudentName = "Maria"
        private val dummyCourseName = "LEIM"
        private val dummyStudentNewName = "Mariana"
    }
}
