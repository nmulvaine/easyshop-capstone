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

	// Get all categories //

	@Override
	public List<Category> getAllCategories()
	{
		List<Category> categories = new ArrayList<>();
		String query = "SELECT * FROM categories";

		try (Connection conn = getConnection())
		{
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next())
			{
				Category category = mapRow(resultSet);
				categories.add(category);
			}
			return categories;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}


	// Get By ID //

	@Override
	public Category getById(int categoryId)
	{
		String query = "SELECT * FROM categories WHERE category_id =?";
		Category category = null;

		try (Connection conn = getConnection())
		{
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, categoryId);

			ResultSet row = stmt.executeQuery();

			if (row.next())
			{
				category = mapRow(row);
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		return category;
	}

	// Create Category //

	@Override
	public Category create(Category category)
	{
		String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
		try (Connection connection = getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS))
		{
			statement.setString(1, category.getName());
			statement.setString(2, category.getDescription());
			statement.executeUpdate();

			try (ResultSet generatedKeys = statement.getGeneratedKeys())
			{
				if (generatedKeys.next())
				{
					category.setCategoryId(generatedKeys.getInt(1));
				}
				else
				{
					throw new SQLException("Error while creating category");
				}
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		return category;
	}

	// Update Category //

	@Override
	public void update(int categoryId, Category category)
	{
		String query = """
				UPDATE categories
				SET name =?, description =?
				WHERE category_id =?
				""";

		try (Connection conn = getConnection())
		{
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, categoryId);
			stmt.setString(2, category.getName());
			stmt.setString(3, category.getDescription());

			int rows = stmt.executeUpdate();

			System.out.println("Rows:" + rows + ". Have been updated.");
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	// Delete Category //

	@Override
	public void delete(int categoryId)
	{
		String query = "DELETE FROM categories WHERE category_id=?";

		try (Connection conn = getConnection())
		{
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, categoryId);

			int rows = stmt.executeUpdate();
			System.out.println("Category: " + categoryId + " deleted.");
			System.out.println("Rows:" + rows + ". Have been deleted.");
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	// Map Row to Category //

	private Category mapRow(ResultSet row) throws SQLException
	{
		int categoryId = row.getInt("category_id");
		String name = row.getString("name");
		String description = row.getString("description");

		Category category = new Category()
		{{
			setCategoryId(categoryId);
			setName(name);
			setDescription(description);
		}};

		return category;
	}

}
