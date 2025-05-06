all:do

do:./gradle
	@echo "cleaning and running..."
	@./gradlew :app:clean :app:run --no-daemon