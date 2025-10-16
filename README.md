1. Cài docker desktop, pull image: mysql, redis
2. Chạy img mysql : docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql
3. Chạy img redis : docker run --name redis-container -p 6379:6379 -d redis
4. Clone dự án load maven, depen...
