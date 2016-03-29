package bronz.accounting.bunk.products.model;


/**
 * Business model for product table.
 */
public class Product
{
	private int productId;
	private String productName;
	
	/**
	 * Default constructor.
	 */	
	public Product()
	{
		super();
	}

	/**
	 * Gets productId.
	 *
	 * @return productId
	 */
	public int getProductId()
	{
		return productId;
	}

	/**
	 * Sets productId.
	 *
	 * @param productId the productId to set
	 */
	public void setProductId( int productId)
	{
		this.productId = productId;
	}

	/**
	 * Gets productName.
	 *
	 * @return productName
	 */
	public String getProductName()
	{
		return productName;
	}

	/**
	 * Sets productName.
	 *
	 * @param productName the productName to set
	 */
	public void setProductName( final String productName )
	{
		this.productName = productName;
	}
}
