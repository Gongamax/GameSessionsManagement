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
        val stdNumber = (1000..9999).random()
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into students values ($stdNumber, 'Joao', 1)")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from students where number = $stdNumber")
            val rs = stm.executeQuery()
            assertTrue(rs.next())
            assertEquals(stdNumber, rs.getInt("number"))
            assertEquals("Joao", rs.getString("name"))
            assertEquals(1, rs.getInt("course"))

            // Clean up
            val stm2 = it.prepareStatement("delete from students where number = $stdNumber")
            val rs2 = stm2.executeUpdate()
            assertEquals(1, rs2)
        }
    }

    @Test
    fun `insert students test`(){
        val number = 17777
        val name = "Maria"
        val course = 1

        dataSource.connection.use {
            val stm = it.prepareStatement("insert into students values ($number,'$name',1)")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
        dataSource.connection.use{
            val stm = it.prepareStatement("select * from students where number = $number")
            val rs = stm.executeQuery()
            assert(rs.next())
            assertEquals(number,rs.getInt("number"))
            assertEquals(name,rs.getString("name"))
            assertEquals(course,rs.getInt("course"))
        }
        dataSource.connection.use {
            val stm = it.prepareStatement("delete from students where number = $number")
            val rs = stm.executeUpdate()
            assertEquals(1,rs)
        }
    }

    @Test
    fun `Insert new course and new student and update name by them number and course of the student`() {
        val courseNumber = 2
        val courseName = "LEIM"
        val studentNumber = 17777
        val studentName = "Maria"

        // Insert new course
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into courses(name) values ('$courseName')")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }

        // Insert new student
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into students values ($studentNumber,'$studentName',$courseNumber)")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }

        // Verify if the course was inserted
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from courses where cid = $courseNumber")
            val rs = stm.executeQuery()
            assert(rs.next())
            assertEquals(courseName, rs.getString("name"))
        }

        // Verify if the student was inserted
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from students where number = $studentNumber")
            val rs = stm.executeQuery()
            assert(rs.next())
            assertEquals(studentName, rs.getString("name"))
            assertEquals(courseNumber, rs.getInt("course"))
        }

        // Update the student name
        val newName = "Mariana"
        dataSource.connection.use {
            val stm = it.prepareStatement("update students set name = '$newName' where number = $studentNumber and course = $courseNumber")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }

        // Verify if the student name was updated
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from students where number = $studentNumber")
            val rs = stm.executeQuery()
            assert(rs.next())
            assertEquals(newName, rs.getString("name"))
        }

        // Clean up
        dataSource.connection.use {
            val stm = it.prepareStatement("delete from students where number = $studentNumber")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
        dataSource.connection.use {
            val stm = it.prepareStatement("delete from courses where cid = $courseNumber")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
        }
    }
}
