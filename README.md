<packaging>pom</packaging>
<description>Kalaha Game</description>
<groupId>com.kalaha.game</groupId>
<artifactId>kalaha-game</artifactId>
<version>1.0.0-SNAPSHOT</version>
<name>Kalaha Game Aggregator Service</name>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.1.RELEASE</version>
		<relativePath/>
	</parent>

	<modules>
		<module>kalaha-game-api</module>
		<module>kalaha-game-impl</module>
	</modules>