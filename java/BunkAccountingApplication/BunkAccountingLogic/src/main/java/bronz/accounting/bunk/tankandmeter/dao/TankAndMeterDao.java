package bronz.accounting.bunk.tankandmeter.dao;

import java.util.List;

import bronz.accounting.bunk.tankandmeter.exception.TankAndMeterDaoException;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.MeterTransaction;
import bronz.accounting.bunk.tankandmeter.model.Tank;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.tankandmeter.model.TankStock;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;

public interface TankAndMeterDao
{
    List<Tank> getAllTanks() throws TankAndMeterDaoException;
    
    List<TankClosingStock> getTankClosingByDate( final int date )
        throws TankAndMeterDaoException;
    
    List<TankTransaction> getAllTankTransByDetail( final String type )
        throws TankAndMeterDaoException;
    
    void saveTankStock( final List<TankStock> tankStockList )
        throws TankAndMeterDaoException;
    
    void saveTankTransactions( final List<TankTransaction> tankTransactions )
        throws TankAndMeterDaoException;
    
    List<MeterClosingReading> getAllMeterClosingByDate( final int date ) throws TankAndMeterDaoException;
    List<MeterClosingReading> getActiveMeterClosings() throws TankAndMeterDaoException;
    
    void saveMeterTransactions( final List<MeterTransaction> meterTransactions ) throws TankAndMeterDaoException;
}
