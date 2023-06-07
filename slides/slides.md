---
marp: true
author: Yash
side: 4:3
title: Blood Donation
title-slide-attributes:
    data-background-image: background.jpg
    data-background-size: contain
---

# Front End 
:::::::::::::: {.columns}
::: {.column width="50%"}
![Sign up Page](../images/img1.png)
:::
::: {.column width="50%"}
![Login Page](../images/img2.png)
:::
::::::::::::::

# Front End
:::::::::::::: {.columns}
::: {.column width="50%"}
![Home Page](../images/img3.png)
:::
::: {.column width="50%"}
- Home Page
    1. Blood Donation
    2. Receive Blood
    3. Blood Emergency
    4. Available Blood Group
:::
::::::::::::::

# Front End
:::::::::::::: {.columns}
::: {.column width="50%"}
![Side Bar](../images/img4.png)
:::
::: {.column width="50%"}
![Change Password](../images/img5.png)
:::
::::::::::::::

# Front End
:::::::::::::: {.columns}
::: {.column width="50%"}
![Date of Submission](../images/img6.png)
:::
::: {.column width="50%"}
![Emergency Blood Needed](../images/img9.png)
:::
::::::::::::::

# Front End
:::::::::::::: {.columns}
::: {.column width="50%"}
![Donor Appointment](../images/img7.png)
:::
::: {.column width="50%"}
- Donor
1. validation  
    email, password
2. userinfo  
    email, hospital, date of submit 
:::
::::::::::::::

# Front End
:::::::::::::: {.columns}
::: {.column width="50%"}
![Receiver Appointment](../images/img8.png)
:::
::: {.column width="50%"}
- Receiver
1. validation  
    email, password
2. userinfo  
    email, hospital, date of recieve
    - today means emergency 
    - not today means not emergency  

:::
::::::::::::::

# Front End
:::::::::::::: {.columns}
::: {.column width="50%"}
![Available Blood Group](../images/img10.png)
:::
::: {.column width="50%"}
![Search Bar](../images/img11.png)
:::
::::::::::::::

# Working
![](../images/web-service.png)

# Working
- User Input is collected by Java
- Create new thread send query by PHP
- PHP serialize output as JSON
- Result is handled by Main Thread of Java
- Which deserialized JSON result and Displays on Screen
