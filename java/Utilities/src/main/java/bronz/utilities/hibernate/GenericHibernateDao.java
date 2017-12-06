package bronz.utilities.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GenericHibernateDao
{
	private static final Logger LOG = LogManager.getLogger( GenericHibernateDao.class );
   private static Map<String, SessionFactory> SESSION_FACTORY_MAP =
      new HashMap<String, SessionFactory>();
   private static ThreadLocal<Session> THREAD_LOCAL =
       new ThreadLocal<Session>();
   
   protected final SessionFactory hibernateSessionFactory;
   
   
   public static SessionFactory getSessionFactory(
         final String configFile,
         final Map<String, String> propertiesToOverride )
   {
      if ( !SESSION_FACTORY_MAP.containsKey( configFile ) )
      {
         final Configuration configuration = new Configuration();
         configuration.configure( configFile );
         if ( null != propertiesToOverride )
         {
             for ( Map.Entry<String, String> entry :
                     propertiesToOverride.entrySet() )
             {
                 configuration.setProperty( entry.getKey(), entry.getValue() );
             }
             
         }
         SESSION_FACTORY_MAP.put( configFile,
               configuration.buildSessionFactory() );
      }
      return SESSION_FACTORY_MAP.get( configFile );
   }
   
   /**
    * Constructor 
    *
    * @param configFile
    */
   public GenericHibernateDao( final String configFile )
   {
       this( configFile, null );
   }
   
   /**
    * Constructor 
    *
    * @param configFile
    */
   public GenericHibernateDao( final String configFile,
           final Map<String, String> propertiesToOverride )
   {
      this.hibernateSessionFactory = getSessionFactory( configFile,
              propertiesToOverride );
   }
   
   public Session getSession()
   {
       final Session session;
       if ( null == THREAD_LOCAL.get() )
       {
           session = this.hibernateSessionFactory.openSession();
           THREAD_LOCAL.set( session );
       }
       else
       {
           session = THREAD_LOCAL.get(); 
       }
       return session;
   }
   
   public void closeSession()
   {
       final Session session = THREAD_LOCAL.get();
       if ( null != session )
       {
           session.clear();
           session.close();
       }
       THREAD_LOCAL.remove();
   }
   
   public void start()
   {
       final Session session = getSession();
       if ( null != session )
       {
           session.beginTransaction();
       }
   }
   
   public void commit()
   {
       final Session session = getSession();
       if ( null != session )
       {
           session.getTransaction().commit();
       }
   }
   
   public void rollback()
   {
       try {
           final Session session = getSession();
           if ( null != session )
           {
               session.getTransaction().rollback();
           }
       } catch (Throwable e) {
           LOG.error("Failed to rollback transactions", e);
       }

   }
   
   public <T> T getById( final Class<T> clazz, final Serializable id )
   {
      final Session session = getSession();
      return (T) session.get( clazz , id );
   }
   
   public <T> List<T> getByQuery( final Class<T> clazz,
         final String hqlQuery, final Object... parameters )
   {
      return (List<T>) fetchByQuery(clazz, hqlQuery, null, parameters);
   }

    public <T> List<T> getByQueryWithLimit( final Class<T> clazz,
        final String hqlQuery, final int maxResults, final Object... parameters )
    {
        return (List<T>) fetchByQuery( clazz, hqlQuery, maxResults, parameters );
    }
   
   public <T,V> List<V> getPropertyByQuery( final Class<T> clazz,
		   final Class<V> propertyClazz, final String hqlQuery,
           final Object... parameters )
   {
	   return (List<V>) fetchByQuery( clazz, hqlQuery, null, parameters );
   }
   
   private <T> List fetchByQuery( final Class<T> clazz, final String hqlQuery, final Integer maxResults,
           final Object... parameters )
   {
      final Session session = getSession();
      final Query queryObject = session.createQuery( hqlQuery );
       if(maxResults != null) {
           queryObject.setMaxResults(maxResults);
       }
      fillParameters( queryObject, parameters );
      return (List) queryObject.list();
   }
   
   public <T> Iterator<T> getIteratorByQuery( final Class<T> clazz,
		   final String hqlQuery, final Object... parameters )
   {
      final Session session = getSession();
      final Query queryObject = session.createQuery( hqlQuery );
      fillParameters( queryObject, parameters );
      return (Iterator<T>) queryObject.iterate();
   }
   
   public <T> List<T> getByCriterias( final Class<T> clazz,
         final Criterion... criterion )
   {
      return (List<T>) fetchByCriterias( clazz, criterion );
   }
   
   private <T> List fetchByCriterias( final Class<T> clazz,
	         final Criterion... criterions )
   {
      final Session session = getSession();
      final Criteria queryObject = session.createCriteria( clazz );
      for ( Criterion criterion : criterions )
      {
          queryObject.add( criterion );
      }
      return (List) queryObject.list();
   }
   
   public void insert( final Object attr )
   {
       final Session session = getSession();
       session.save( attr );
       for ( String getterName : getCollectionGetterNames( attr ))
       {
           for ( Object childAttr : getCollectionByFieldName( attr, getterName ))
           {
               insert( childAttr );
           }
       }
   }
   
   public void delete( final Object attr )
   {
       final Session session = getSession();
       for ( String getterName : getCollectionGetterNames( attr ))
       {
           for ( Object childAttr : getCollectionByFieldName( attr, getterName ))
           {
               delete( childAttr );
           }
       }
       session.delete( attr );
   }
   
   public void update( final Object attr )
   {
       final Session session = getSession();
       session.update( attr );
       for ( String getterName : getCollectionGetterNames( attr ))
       {
           for ( Object childAttr : getCollectionByFieldName( attr, getterName ))
           {
               update( childAttr );
           }
       }
   }
   
   public void executeHQL( final String hqlQuery, final Object... parameters )
   {
	   final Session session = getSession();
	   final Query queryObject = session.createQuery( hqlQuery );
	   fillParameters( queryObject, parameters );
	   queryObject.executeUpdate();
   }
   
   public Object executeSQL( final String sqlQuery, final String scalarName,
           final NullableType type )
   {
       final Session session = getSession();
       final Query queryObject = session.createSQLQuery(
               sqlQuery ).addScalar( scalarName, type );
       return queryObject.uniqueResult();
   }
   
   private String getSavedSQLQueryString( final String queryName)
   {
       final Session session = getSession();
       return session.getNamedQuery(queryName).getQueryString();
   }
   
   public SQLQuery getSavedSQLQuery( final String queryName)
   {
       final Session session = getSession();
       final SQLQuery query = session.createSQLQuery( getSavedSQLQueryString(queryName) );
       query.setReadOnly(true);
       return query;
       
   }
   
   @SuppressWarnings("deprecation")
   public void executeSQL( final String sqlStatement, final Object... objects)
   {
	   try
	   {
		   final Connection connection = getSession().connection();
		   final PreparedStatement statement = connection.prepareStatement(
				   sqlStatement );
		   if ( null != objects )
		   {
			   int i = 1;
			   for ( Object object : objects )
			   {
				   if( String.class.isInstance( object ) )
				   {
					   statement.setString( i++, (String) object );
				   }
				   else if ( Integer.class.isInstance( object ) )
				   {
					   statement.setInt( i++, (Integer) object );
				   }
				   else if ( BigDecimal.class.isInstance( object ) )
				   {
					   statement.setBigDecimal( i++, (BigDecimal) object );
				   }
				   else
				   {
					   throw new GenericDaoException( "Unsupported arg type:" +
							   object.getClass().getName() );
				   }
			   }
		   }
		   statement.executeUpdate();
	   }
	   catch ( SQLException exception )
	   {
		   throw new GenericDaoException( exception );
	   }
   }
   
   @SuppressWarnings("deprecation")
   public Connection getConnection()
   {
       return getSession().connection();
   }

   public void fillParameters( final Query query, final Object... parameters )
   {
	   if ( null != parameters )
	   {
		   int i = 0;
		   for ( Object object : parameters )
		   {
			   if( String.class.isInstance( object ) )
			   {
				   query.setString( i++, (String) object );
			   }
			   else if ( Integer.class.isInstance( object ) )
			   {
				   query.setInteger( i++, (Integer) object );
			   }
			   else if ( BigDecimal.class.isInstance( object ) )
			   {
				   query.setBigDecimal( i++, (BigDecimal) object );
			   }
			   else
			   {
				   query.setParameter( i++, object );
			   }
		   }
	   }
	   
   }
   
   private List<String> getCollectionGetterNames( final Object attr )
   {
      final List<String> collectionGetterNames = new ArrayList<String>();
      final ClassMetadata classMetaData =
         this.hibernateSessionFactory.getClassMetadata( attr.getClass() );
      final Type[] fieldTypes = classMetaData.getPropertyTypes();
      final String[] fieldNames = classMetaData.getPropertyNames();
      
      for ( int i = 0; i < fieldNames.length ; i++ )
      {
         if ( fieldTypes[ i ].isCollectionType() )
         {
            final String field = fieldNames[ i ];
            collectionGetterNames.add( "get" +
                  field.substring( 0, 1 ).toUpperCase() + field.substring( 1 ));
         }
      }
      return collectionGetterNames;
   }
   
   private Collection<? extends Object> getCollectionByFieldName(
         final Object object, final String getterName)
   {
      try
      {
         final Method getterMethod = object.getClass().getMethod( getterName );
         return (Collection<? extends Object>) getterMethod.invoke( object );
      }
      catch (Exception exception)
      {
         throw new GenericDaoException( exception );
      }
   }
   
   public void closeConnection(final Connection connection)
   {
       if (connection != null)
       {
           try
           {
               connection.close();
               closeSession();
           }
           catch( SQLException sqlException )
           {
               //Ground the exception.
               LOG.error( "Failed to close connection", sqlException );
           }
       }
   }
   
   public void closeStatement( final Statement statement)
	{
		try
		{
			if ( statement != null )
			{
				statement.close();
			}
		}
		catch ( SQLException sqlException)
		{
			//Ground the exception.
            LOG.error( "Failed to close Statement", sqlException );
		}
	}
	
	public void closeResultSet( final ResultSet resultSet)
	{
		try
		{
			if ( resultSet != null )
			{
				resultSet.close();
			}
		}
		catch ( SQLException sqlException)
		{
			//Ground the exception.
            LOG.error( "Failed to close ResultSet", sqlException );
		}
	}
}
