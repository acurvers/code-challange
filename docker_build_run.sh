docker build -t booking-coding-challenger-img .
docker run -p 8943:8943 -it --rm --name booking-coding-challenger-app booking-coding-challenger-img
