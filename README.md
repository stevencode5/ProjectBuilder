# ProjectBuilder
Web application that modifies the different modules of a maven java business application with modular architecture.


## Directory Structure

ProjectBuilder works with maven java enterprise projects with next directory structure:

```
Project
|_core
	|_core-module #
		|_entities
			|_pom.xml
		|_persistence
			|_pom.xml
		|_plugin
			|_pom.xml
		|_service
			|_pom.xml
		|_tools
			|_pom.xml
		|_web
			|_pom.xml
	|_persistence
		|_service
			|_src
				|_main
					|_resources
						|_META-INF
							|_persistence.xml
	|_web
		|_ui
			|_pom.xml
			
|_dist
	|_ear
		|_pom.xml
	
|_modules
	|_module #
		|_entities
			|_pom.xml
		|_business
			|_pom.xml
		|_persistence
			|_pom.xml
		|_plugin
			|_pom.xml
		|_web
			|_pom.xml
|_pom.xml
```

## Built With

* [Primefaces](https://www.primefaces.org/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Steven Gonzalez** - *Initial work* - [StevenCode5](https://github.com/stevencode5)

See also the list of [contributors](https://github.com/stevencode5/ProjectBuilder/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

