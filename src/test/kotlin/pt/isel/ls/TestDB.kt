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
    fun `test basic update`() {
        dataSource.connection.use {
            val stm = it.prepareStatement("update students set name = 'Joao Silva' where number = 1")
            val rs = stm.executeUpdate()
            assertEquals(1, rs)
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
}
