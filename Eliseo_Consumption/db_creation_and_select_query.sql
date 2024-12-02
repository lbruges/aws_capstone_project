# Create query
CREATE TABLE IF NOT EXISTS `gas_provider`.`gas_consumption` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `month` INT NOT NULL,
  `year` INT NOT NULL,
  `address` VARCHAR(150) NOT NULL,
  `consumption` DOUBLE(5, 2) NOT NULL,
  `zone` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id`),
  UNIQUE INDEX `address_UNIQUE` (`address`),
  INDEX `month_year` (`month`, `year`),
  INDEX `zone` (`zone`));


# Select query
SELECT month, year, avg(consumption), zone FROM gas_consumption WHERE month = :inputMonth AND day = :inputDay AND zone = :inputZone GROUP BY month, year, zone;
