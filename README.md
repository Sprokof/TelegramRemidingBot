![symbol](https://user-images.githubusercontent.com/90979711/150548720-12608103-c91f-4500-b592-a6f6e2fb846f.jpg) 
* avatar picture of Reminder bot

## Description

LongPull telegram bot what implementation reminders fucntion. It's using Spring Boot, lombok library, PostgreSQL database, and Hibernate ORM.
Work of this bot demonstrates next GIF-animation 

![botGif](https://user-images.githubusercontent.com/90979711/158223939-0a0f2242-6bb1-42f9-ba80-6ea1fa9f1d1a.gif)


Users' input maybye not right. To correct work it's implementing input's validate and send appropriate response . Example:

![botGif2](https://user-images.githubusercontent.com/90979711/158227397-93380217-99e6-4abd-95eb-e2326dfcbe3e.gif)

It has next data base scheme.

![Снимок экрана (43)](https://user-images.githubusercontent.com/90979711/160174310-86910423-78a9-4987-8406-432ad01b93f8.png)

* thats tables has oneToOne relationship and all users' reminders is recorded into table in encrypt view. 

![Снимок экрана (40)](https://user-images.githubusercontent.com/90979711/160175520-ff7cf0ec-616d-4df0-b76d-a3da8148db21.png)

* It achives by XOR decoded.
 

## Docker
It's using docker, images of bot and postgres may be find on next link
https://hub.docker.com/repository/docker/1s2s5/telegram_bot

## Project's structure
Pictures under is showing it's structure

![Снимок экрана (31)](https://user-images.githubusercontent.com/90979711/160171614-e315a65d-0554-495a-8080-cccf1d8b1f99.png)

![Снимок экрана (51)](https://user-images.githubusercontent.com/90979711/161578585-7aa8908b-02a9-4b73-832c-ae51b0d58c97.png)

![Снимок экрана (35)](https://user-images.githubusercontent.com/90979711/160171662-4c632e30-59b0-4b8f-887d-bc72fa4df2d1.png)
