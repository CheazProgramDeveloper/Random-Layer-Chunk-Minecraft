🌍 Random Chunk Layers
A mod for Minecraft (Fabric 1.21.1) that turns the entire world into a single chunk with random layers of blocks growing downward.

📖 Description
Random Chunk Layers is a mod that limits the world to a single central chunk (by default at coordinates 0,0) and adds new random layers every in‑game day. Inside the chunk, there is grass, wood, and stone, and over time, random blocks, water/lava, chests with useful items, and even mobs appear.

The mod was created specifically for a challenge: survive for 100 days in a single chunk, gather resources, and get an achievement!

🎮 Features
One chunk — the entire world is limited to one chunk (you can adjust the coordinates).
Daily layer growth — every new day, 3 random layers of blocks appear under the chunk (the number is adjustable).
The first layers are fixed — grass, wood, stone (to give you something to start with).
Random blocks — all game blocks are used (except for lag‑prone ones: flowers, signs, carpets, doors, beds, pots, gates, fences, corals).
Rare water/lava — up to 2 blocks of water or lava can appear in each layer.
Peaceful mobs — sheep, chickens, cows, pigs, rabbits, and horses appear every 5 days.
Hostile mobs — zombies, skeletons, spiders, and creepers appear every 3 nights (the number depends on the difficulty).
Loot chest — every 10 days, a chest with useful items appears (iron, diamonds, emeralds, food, tools, armor).
Day counter — displayed in the action bar [Nick] — Day X — YY% (day progress).
Auto‑collect drop — all broken blocks immediately go into the inventory (requires the correct tool).
Bed — a bed of a random color appears in the center of the chunk; you can sleep as usual.
Time acceleration — the /speedtime set [1-100] command (1 — normal speed, 100 — day/night per second).
Achievement — for living 100 days, the “One Hundred Days in a Chunk” achievement is awarded (with a clock icon and 1000 experience).

📦 Installation
1. Make sure you have Fabric Loader installed for version 1.21.1.
2. Download Fabric API for 1.21.1 and place it in the .minecraft/mods folder.
3. Download the randomchunk-1.0.3.jar mod itself and place it in the same mods folder.
4. Launch the game with the Fabric profile.
5. Important: use Minecraft 1.21.1, not snapshots.

🎯 Gameplay
Game start: You appear on the surface of the central chunk (default coordinates: 0,0).
There is grass on the chunk, a tree below it, and even lower a stone.
In the center there is a bed (random color).
In the action bar you see the day count
Every day at midnight (or when you are sleeping), 3 new layers of random blocks appear under the chunk.
The day counter increases by 1.
Peaceful mobs spawn every 5 days.
A chest with loot appears every 10 days.
Hostile mobs spawn every 3 nights.
On the 100th day, you receive an achievement.

📋 Requirements
Minecraft 1.21.1
Fabric Loader (latest version)
Fabric API (for version 1.21.1)
Optional:
Dynamic Height — to increase the world height to 1000 blocks (recommended if you want more layers) - https://modrinth.com/mod/dynamic-height

👨‍💻 Author
Cheaz is the creator of the mod.
If you have any questions or suggestions, please write on GitHub or in Telegram.

❓ Frequently Asked Questions (FAQ)
1. Why do I fall into the void when I enter?
Answer: Make sure you have enabled the gamerule /gamerule randomChunk true. If the chunk hasn’t been created, use /randomchunk init.
2. How do I increase the world height to 1000 blocks?
Answer: Install the Dynamic Height mod, edit its config (set the height to 1000), and restart the world.
3. Why don’t mobs spawn?
Answer: Check the world difficulty. On “Peaceful” difficulty, hostile mobs do not spawn. Peaceful mobs spawn only every 5 days.
4. Can I play on a server?
Answer: Yes, the mod works on a server, but all players will be tied to the same chunk (unless you change the coordinates in the config).
5. How do I reset progress?
Answer: Use the /randomchunk reset command or delete the randomchunk_state.dat file in the world folder.
____________________________

🌍 Случайные слои чанка
Мод для Minecraft (Fabric 1.21.1), который превращает весь мир в один чанк со случайными слоями блоков, растущими вниз.

📖 Описание
Random Chunk Layers — это мод, который ограничивает мир одним центральным чанком (по умолчанию с координатами 0,0) и добавляет новые случайные слои каждый игровой день. Внутри чанка есть трава, дерево и камень, а со временем появляются случайные блоки, вода/лава, сундуки с полезными предметами и даже мобы.

Мод был создан специально для челленджа: продержитесь 100 дней в одном чанке, соберите ресурсы и получите достижение!

🎮 Особенности
Один чанк — весь мир ограничен одним чанком (координаты можно изменить).
Ежедневное увеличение слоя — каждый новый день под чанком появляется 3 случайных слоя блоков (количество можно изменить).
Первые слои фиксированы — трава, дерево, камень (чтобы вам было с чего начать).
Случайные блоки — используются все игровые блоки (кроме тех, которые могут вызывать лаги: цветы, знаки, ковры, двери, кровати, горшки, ворота, заборы, кораллы).
Редкая вода/лава — в каждом слое может появиться до 2 блоков воды или лавы. 
Мирные мобы — овцы, куры, коровы, свиньи, кролики и лошади появляются каждые 5 дней. 
Враждебные мобы — зомби, скелеты, пауки и криперы появляются каждые 3 ночи (количество зависит от сложности). 
Сундук с добычей — каждые 10 дней появляется сундук с полезными предметами (железом, алмазами, изумрудами, едой, инструментами, броней). 
Счетчик дней — отображается на панели действий [Ник] — День X — YY% (прогресс за день). 
Автосбор — все разрушенные блоки сразу попадают в инвентарь (требуется подходящий инструмент). 
Кровать — в центре чанка появляется кровать случайного цвета; на ней можно спать как обычно. 
Ускорение времени — команда /speedtime set [1-100] (1 — обычная скорость, 100 — смена дня и ночи каждую секунду). 
Достижение — за 100 дней жизни выдается достижение «Сто дней в чанке» (со значком часов и 1000 опыта). 

📦 Установка
1. Убедитесь, что у вас установлен Fabric Loader для версии 1.21.1.
2. Скачайте Fabric API для версии 1.21.1 и поместите его в папку .minecraft/mods.
3. Скачайте сам мод randomchunk-1.0.3.jar и поместите его в ту же папку с модами.
4. Запустите игру с профилем Fabric.
5. Важно: используйте Minecraft 1.21.1, а не его снэпы.

🎯 Игровой процесс
Начало игры: вы появляетесь на поверхности центрального чанка (координаты по умолчанию: 0,0).
На чанке есть трава, под ним — дерево, а еще ниже — камень.
В центре находится кровать (цвет случайный).
На панели действий отображается количество прошедших дней
Каждый день в полночь (или когда вы спите) под чанком появляются 3 новых слоя случайных блоков.
Счетчик дней увеличивается на 1.
Мирные мобы появляются каждые 5 дней.
Сундук с добычей появляется каждые 10 дней.
Враждебные мобы появляются каждые 3 ночи.
На 100-й день вы получаете достижение.

📋 Требования
Minecraft 1.21.1
Загрузчик Fabric (последняя версия)
Fabric API (для версии 1.21.1)
Необязательный:
Динамическая высота — для увеличения высоты мира до 1000 блоков (рекомендуется, если вы хотите увеличить количество слоев) - https://modrinth.com/mod/dynamic-height

👨‍💻 Автор
Cheaz — создатель мода. 
Если у вас есть вопросы или предложения, пишите нам на GitHub или в Telegram. 

❓ Часто задаваемые вопросы (FAQ)
1. Почему при входе я проваливаюсь в пустоту? 
Ответ: убедитесь, что вы включили параметр /gamerule randomChunk true. Если чанк не был создан, используйте /randomchunk init. 
2. Как увеличить высоту мира до 1000 блоков? 
Ответ: установите мод Dynamic Height, отредактируйте его настройки (установите высоту 1000) и перезапустите мир. 
3. Почему не появляются мобы? 
Ответ: проверьте сложность мира. На уровне сложности «Мирный» враждебные мобы не появляются.  Мирные мобы появляются только раз в 5 дней. 
4. Можно ли играть на сервере? 
Ответ: да, мод работает на сервере, но все игроки будут привязаны к одному и тому же чанку (если вы не измените координаты в настройках). 
5. Как сбросить прогресс? 
Ответ: используйте команду /randomchunk reset или удалите файл randomchunk_state.dat в папке с миром.
