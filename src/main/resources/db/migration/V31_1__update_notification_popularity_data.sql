UPDATE notification
SET popularity = UPPER(popularity)
WHERE popularity IN ('hot','normal');