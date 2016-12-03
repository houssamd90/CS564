-------------------------------------------------------------------------------
-- NAME:         add_transaction
-- DESCRIPTION: Adds a stock transaction for a particular user
-- PARAMETERS:
--  id        integer - User id to add a stock transaction for.
--  curDate   date    - The date when the particular stock transaction occurred
--  curTicker text    - The stock ticker that this particular stock transaction impacts
--  curAmount numeric - Amount of stock bought/sold.
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION add_transaction(id integer, curDate date, curTicker text, curAmount numeric(12,3)) RETURNS void AS $$
BEGIN
	INSERT INTO Strategies(userID,date,ticker,amount) values(id,curDate,curTicker,curAmount);
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME:        current_holdings
-- DESCRIPTION: Gives the current holdings of a particular stock ticker at a given date
-- PARAMETERS:
--  curUser   integer - The user id to check holdings for
--  curDate   date    - The effective date for transactions
--  curTicker text    - The ticker to determine the holdings for
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION current_holdings(curUser integer, curDate date, curTicker text) RETURNS numeric(12,2) AS $$
BEGIN
	SELECT Market_Observations.ticker,sum(amount) 
		FROM Investors,Strategies
		WHERE Investors.userid = Strategies.userid AND
			Investors.userid = curUser AND
			Strategies.ticker = curTicker AND
			Strategies.date <= curDate;
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: current_tickers
-- DESCRIPTION: Gives a list of all stock tickers active at a given time, and how much of them
--              the given user owns.
-- PARAMETERS: 
--  curUser integer - User ID to summarize stock holdings for
--  curDate date    - Efffective date from which to summarize stock holdings
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION current_tickers(curUser integer, curDate date) RETURNS TABLE (ticker text, amount numeric(12,3)) as
$$
BEGIN
	SELECT ticker,sum(count) 
		FROM Strategies 
		WHERE userid = curUser AND 
			Strategies.date <= curDate 
		GROUP BY userid,ticker
		HAVING sum(count)>0; 
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME:        cpi_adjust
-- DESCRIPTION: Returns the percentage of inflation since our first stock observation
-- PARAMETERS: 
--  curDate date - Effective date for calculating inflation
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION cpi_adjust(curDate date) RETURNS numeric(12,2) AS $$
DECLARE minimum_cpi numeric(12,2);
DECLARE maximum_cpi numeric(12,2);
BEGIN
	SELECT min(cpi) 
		INTO minimum_cpi 
		FROM CPI;
	SELECT cpi 
		INTO maximum_cpi 
		FROM CPI 
		WHERE to_char(curDate,'YYYY-MM') = to_char(date,'YYYY-MM');
	RETURN  minimum_cpi / (maximum_cpi - minimum_cpi);
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME:        net_worth
-- DESCRIPTION: Calculates the total value of stock holdings for the user.
-- PARAMETERS:
--  userID  integer - ID for the user to calculate net worth for.
--  curDate date    - Effective date to examine net worth
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION net_worth(userID integer, curDate date) RETURNS numeric(12,2) AS $$
BEGIN
SELECT sum(stock_values.value) 
	FROM (SELECT P.count * M.price AS value
		FROM current_tickers(userID,curDate) P, Market_Observatons M
		WHERE P.ticker = M.ticker) AS stock_values;
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: net_worth_trending
-- DESCRIPTION: For a given userID, gives their net worth from the entire span
--              of time bracketed by their transactions.
-- PARAMETERS:
--  id integer - User ID for trending net worth
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION net_worth_trending(id integer) RETURNS TABLE (curDate date, curNetWorth numeric(12,2)) AS $$
DECLARE curDate date; 
DECLARE endDate date;
DECLARE curNetWorth numeric(12,2);
BEGIN
	SELECT min(date) 
		INTO curDate 
		FROM Strategies.date 
		WHERE Strategies.userID = id;
	SELECT max(date) 
		INTO endDate 
		FROM Strategies.date 
		WHERE Strategies.userID = id;
	LOOP
		curDate := curDate + 1;
		curNetWorth := current_holdings(id,curDate);
		RETURN NEXT;
		EXIT WHEN curDate>=endDate;
	END LOOP;
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: remove_transaction
-- DESCRIPTION: Removes an identified transaction from a given user
-- PARAMETERS:
--  id        integer - User ID to remove the transaction from
--  curDate   date    - The date of the transaction to remove
--  curTicker text    - The ticker to update
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION remove_transaction(id integer, curDate date, curTicker text) RETURNS void AS $$
BEGIN
	DELETE FROM Strategies 
		WHERE userID=id AND 
			date=curDate AND 
			ticker=curTicker;
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: change_cash
-- DESCRIPTION: Create a new entry for the current cash on hand
-- PARAMETERS:
--  id      integer - User ID for cash on hand
--  curDate date    - The date that a person's cash holdings changed.
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION change_cash(id integer, curDate date, curAmount numeric(12,2) ) RETURNS void AS $$
BEGIN
	INSERT INTO cash_on_hand(userID,date,cash) 
		values(id,curDate,curAmount);
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: current_cash
-- DESCRIPTION: Finds the effective cash_on_hand for a given date
-- PARAMETERS:
--  id      integer - User ID to find cash holdings for
--  curDate date    - Effective date to return the cash holdings for
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION current_cash(id integer, curDate date) RETURNS numeric (12,2) AS $$
DECLARE lookback_date date; 
BEGIN
	SELECT max(date) 
		INTO lookback_date 
		FROM cash_on_hand 
		WHERE userID = id AND 
			date <= curDate;
	SELECT cash FROM cash_on_hand where date = lookback_date AND userID = id;
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: current_holdings
-- DESCRIPTION: Returns current holdings for a particular date
-- PARAMETERS:
--  id      integer - User id to return holdings for
--  curDate date    - Effective date to return holdings for
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION current_holdings(id integer, curDate date) RETURNS numeric(12) AS $$
BEGIN
	RETURN current_cash(id,curDate) + net_worth(id,date);
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: delete_config
-- DESCRIPTION: Deletes all of the user input information
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION delete_config() RETURNS void AS $$
BEGIN
	DELETE FROM Strategies;
	DELETE FROM Cash_On_Hand;
	DELETE FROM Investors;
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: all_transactions
-- DESCRIPTION: Returns all transactions for a given user.
-- PARAMETERS:
--  id integer - User ID to return all transactions for
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION all_transactions(id integer) RETURNS TABLE (xactDate date, xactTicker text, xactCount numeric(12,3)) AS $$
BEGIN
	SELECT date,ticker,count 
		FROM Strategies
		WHERE id=userID; 	
END
$$ LANGUAGE plpgsql;

-------------------------------------------------------------------------------
-- NAME: add_new_user
-- DESCRIPTION: Creates a new blank user with no current cash holdings
-- PARAMETERS:
--  id   integer - ID for the user
--  name text    - Name of the user to create
--  dob  date    - Date this user was born 
--  coh  numeric - Initial cash on hand for this user
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION add_new_user(id integer, usrName text, dob date, coh numeric(12,2)) RETURNS void AS $$
BEGIN
	INSERT INTO Investors(userid,name,date_of_birth) values(id,usrName,dob);
	INSERT INTO Cash_On_Hand(date,userid,cash) values('1980-01-02',id,coh);	
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION oldest_observation(curTicker text) RETURNS date AS $$
BEGIN
	SELECT min(date) from Market_Observations WHERE ticker=curTicker;
END
$$ LANGUAGE plpgsql;
