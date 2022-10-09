#!/bin/bash

printf "\e[32;1mEnter plugin name:\n\e[37;0m> "
read plugin_name

# To lower case
plugin_name=$(echo "$plugin_name" | tr '[:upper:]' '[:lower:]')

pattern="^[a-z]+$"
if [[ $plugin_name =~ $pattern ]]
then
  echo "Creating plugin with name: '$plugin_name'."
else
  echo "Invalid name, please use only lower case letters."
  exit 1
fi

printf "\n\e[32;1mEnter plugin author name:\n\e[37;0m> "
read plugin_author

printf "\n\e[32;1mDoes it needs database access? [Y/n]:\n\e[37;0m> "
while true; do
    read -n1 yn
    case $yn in
        [Nn]* ) uses_shared=false; break;;
        * ) uses_shared=true; break;;
    esac
done

printf "\n\n\e[32;1mDoes it uses economy? [Y/n]:\n\e[37;0m> "
while true; do
    read -n1 yn
    case $yn in
        [Nn]* ) uses_vault=false; break;;
        * ) uses_vault=true; break;;
    esac
done

printf "\n"


# Create directories
mkdir -p plugins/$plugin_name/src/main/kotlin/fr/pickaria/$plugin_name plugins/$plugin_name/src/main/resources

# Create default files

cat > plugins/$plugin_name/src/main/resources/plugin.yml << EOF
name: Pickaria${plugin_name^}
version: 1.0
api-version: 1.19
main: fr.pickaria.$plugin_name.Main
author: $plugin_author
EOF

# Add dependencies to project
if [ "$uses_shared" = true ] || [ "$uses_vault" = true ]
then
  printf "depends: [" >> plugins/$plugin_name/src/main/resources/plugin.yml

  if [ "$uses_shared" = true ] && [ "$uses_vault" = true ]
  then
    printf "PickariaShared, Vault" >> plugins/$plugin_name/src/main/resources/plugin.yml

    cat > plugins/$plugin_name/build.gradle.kts << EOF
repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly(project(":shared"))
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
EOF
  elif [ "$uses_shared" = true ]
  then
    printf "PickariaShared" >> plugins/$plugin_name/src/main/resources/plugin.yml

    cat > plugins/$plugin_name/build.gradle.kts << EOF
dependencies {
	compileOnly(project(":shared"))
}
EOF
  elif [ "$uses_vault" = true ]
  then
    printf "Vault" >> plugins/$plugin_name/src/main/resources/plugin.yml

    cat > plugins/$plugin_name/build.gradle.kts << EOF
repositories {
	maven("https://jitpack.io")
}

dependencies {
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
EOF
  fi

  echo "]" >> plugins/$plugin_name/src/main/resources/plugin.yml
else
  printf "" > plugins/$plugin_name/build.gradle.kts
fi

# Create Main class file
cat > plugins/$plugin_name/src/main/kotlin/fr/pickaria/$plugin_name/Main.kt << EOF
package fr.pickaria.$plugin_name

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		// TODO: Add plugin logic here

		logger.info("${plugin_name^} plugin loaded!")
	}
}
EOF

# Add the project to Gradle
sed -i "s/include([^)]*\b/&\", \"$plugin_name/" settings.gradle.kts
echo "project(\":$plugin_name\").projectDir = File(\"plugins/$plugin_name\")" >> settings.gradle.kts

echo "Your plugin $plugin_name is created, please refresh Gradle now."