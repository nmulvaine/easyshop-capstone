package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }


    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";

        try (Connection conn = getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Category category = mapRow(resultSet);
                categories.add(category);
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Get By ID //

    @Override
    public Category getById(int categoryId)
    {
        String query = "SELECT * FROM Category WHERE category_id =?";
        Category category = null;

        try (Connection conn = getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, categoryId);

            ResultSet row = preparedStatement.executeQuery();

            if (row.next()) {
                return mapRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Create Category //

    @Override
    public Category create(Category category)
    {
        String query = "SELECT * FROM Category WHERE category_id =?";

        try (Connection conn = getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, category.getCategoryId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Category already exists with id: " + category.getCategoryId());
            } else {
                String insertQuery = "INSERT INTO Category (category_id, name, description) VALUES (?,?,?)";
                preparedStatement = conn.prepareStatement(insertQuery);
                preparedStatement.setInt(1, category.getCategoryId());
                preparedStatement.setString(2, category.getName());
                preparedStatement.setString(3, category.getDescription());
                preparedStatement.executeUpdate();
                System.out.println("New category inserted with id: " + category.getCategoryId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Category(category.getCategoryId(), category.getName(), category.getDescription());
    }

    // Update Category //

    @Override
    public void update(int categoryId, Category category)
    {
        String query = "SELECT * FROM category WHERE category_id=?";

        try (Connection conn = getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, category.getCategoryId());
            preparedStatement.setString(2, category.getName());
            preparedStatement.setString(3, category.getDescription());

            int rows = preparedStatement.executeUpdate();
            System.out.println("Rows:" + rows + ". Have been updated.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
// Delete Category //

    @Override
    public void delete(int categoryId)
    {
       String query = "DELETE FROM category WHERE category_id=?";

       try (Connection conn = getConnection())
            {
               PreparedStatement preparedStatement = conn.prepareStatement(query);
               preparedStatement.setInt(1, categoryId);
               preparedStatement.executeUpdate();
               System.out.println("Category with id: " + categoryId + " deleted.");
            } catch (SQLException e) {
               throw new RuntimeException(e);
            }
    }

    // Map Row to Category //

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        return new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};
    }

}
