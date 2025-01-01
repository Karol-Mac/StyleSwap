use styleswap;

insert into roles (name) values ('ROLE_ADMIN');
insert into roles (name) values ('ROLE_USER');
#
# -- creating users
# insert into users (email, name, password, username) values ('admin@email.com', 'admin',
#                                                                    '$2a$10$Kcv0RMReIoG7unGNEdqbaO/oCvDkRHJ6.gjQ47bY6on98Ysvpy/7i', 'admin');
#
# insert into users (email, name, password, username) values ('user1@email.com', 'user1',
#                                                                    '$2a$10$BO38kLt14mkRORfJOf0SRO4XOfsegJIJFLXze50hLqhrlbMXIeJem', 'user1');
#
# insert into users (email, name, password, username) values ('user2@email.com', 'user2',
#                                                                    '$2a$10$47m4oq68rn2phtIzCSIaEuIEA1ONGWEzcR590cRi2lXwwCqmpO0tm', 'user2');
# -- give users specific roles
# insert into users_roles (user_id, role_id) values (1, 1);
# insert into users_roles (user_id,role_id) values (2, 2);
# insert into users_roles (user_id,role_id) values (3, 2);
#
# INSERT INTO categories (name, description, created_at, updated_at)
# VALUES ('T-Shirts', 'Category for various T-Shirts', DATE(NOW()), DATE(NOW()));
# INSERT INTO categories (name, description, created_at, updated_at)
# VALUES ('Jeans', 'Category for different styles of Jeans', DATE(NOW()), DATE(NOW()));
# INSERT INTO categories (name, description, created_at, updated_at)
# VALUES ('Dresses', 'Category for elegant and casual Dresses', DATE(NOW()), DATE(NOW()));
# INSERT INTO categories (name, description, created_at, updated_at)
# VALUES ('Skirts', 'Category for various types of Skirts', DATE(NOW()), DATE(NOW()));
# INSERT INTO categories (name, description, created_at, updated_at)
# VALUES ('Blouses', 'Category for stylish Blouses', DATE(NOW()), DATE(NOW()));
#
#
#
# INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id, is_available)
#             VALUES ('T-Shirt', 'Comfortable cotton t-shirt', 19.99, 'M', DATE(NOW()), DATE(NOW()), 'Cotton', 10, 1, 2, true);
# INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id, is_available)
#             VALUES ('Jeans', 'Stylish blue jeans', 49.99, 'L', DATE(NOW()), DATE(NOW()), 'Denim', 20, 2, 3, true);
# INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id, is_available)
#             VALUES ('Dress', 'Elegant black dress', 79.99, 'S', DATE(NOW()), DATE(NOW()), 'Silk', 15, 3, 2, true);
# INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id, is_available)
#             VALUES ('Skirt', 'Green summer skirt', 29.99, 'M', DATE(NOW()), DATE(NOW()), 'Linen', 5, 4, 3, true);
# INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id, is_available)
#             VALUES ('Blouse', 'White elegant blouse', 39.99, 'L', DATE(NOW()), DATE(NOW()), 'Cotton', 8, 5, 2, false);
# INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id, is_available)
#             VALUES ('White T-Shirt', 'Comfortable cotton t-shirt', 24.99, 'M', DATE(NOW()), DATE(NOW()), 'Cotton', 10, 1, 2, false);
#
# insert into conversations (buyer_id, clothe_id) values (3, 1);
#
# INSERT INTO messages (message, is_buyer, created_at, conversation_id)
#             VALUES ('Hello, I would like to buy this T-Shirt, but half the price', true, DATE(NOW()), 1);
# INSERT INTO messages (message, is_buyer, created_at, conversation_id)
#             VALUES ('No, I won\'t sold it for 10$', false, DATE(NOW()), 1);