docker build --tag modularbank -f db.Dockerfile .

docker run -p 5432:5432 modularbank