<div id="top"></div>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/Pickaria/plugin-collection">
    <img src="https://www.pickaria.fr/assets/images/Bloc.jpg" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Pickaria</h3>

  <p align="center">
    Multi function plugin for the Pickaria Minecraft server.
    <br />
    <br />
    <a href="https://www.pickaria.fr">Try it</a>
    ·
    <a href="https://github.com/Pickaria/plugin-collection/issues">Report Bug</a>
    ·
    <a href="https://github.com/Pickaria/plugin-collection/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Compiling</a></li>
      </ul>
    </li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This is the plugin of the [Pickaria](https://www.pickaria.fr) Minecraft server.



### Built With

* [Gradle](https://gradle.org/)



<!-- GETTING STARTED -->
## Getting Started

This is the instructions on setting up your project locally.
To get a local copy up and running follow these steps.

### Prerequisites

* JDK 17
    * https://adoptium.net/

### Compiling

1. Clone the repo
   ```sh
   git clone https://gitlab.pickaria.fr/pickaria/plugin.git
   ```
2. Create the test server
   ```shell
   ./scripts/setup-server.sh
   ``` 
3. Compile using Gradle
    1. On Linux:
       ```shell
       ./gradlew jar
       ```
    2. On Windows:
       ```shell
       gradlew.bat jar
       ```
4. Start the test server
   ```shell
   ./scripts/start-server.sh
   ``` 
   You can also add a run shortcut in your favourite IDE.


<!-- CONTRIBUTING -->
## Contributing

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- CONTACT -->
## Contact

[@pickaria](https://twitter.com/pickaria) - contact@pickaria.fr

Project Link: [https://github.com/Pickaria/plugin-collection](https://github.com/Pickaria/plugin-collection)