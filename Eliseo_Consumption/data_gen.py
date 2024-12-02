from faker import Faker
import random as rand


def gen_data(rows_to_gen, month, year, zone, min_range, max_range):
    fake = Faker()
    for _ in range(0, rows_to_gen):
        consumption = rand.randint(min_range, max_range)
        address = fake.address().replace("\n", " ")[:149]
        print(f"INSERT INTO `gas_provider`.`gas_consumption`(month, year, address, consumption, zone) VALUES ({month}, {year}, '{address}', {consumption}, '{zone}');")


def gen_data_global(month, year, rows_to_gen, min_range, max_range):
    zones = ["A", "B", "C", "D"]

    for zone in zones:
        gen_data(rows_to_gen, month, year, zone, min_range, max_range)


gen_data_global(12, 2024, 100, 180, 300)