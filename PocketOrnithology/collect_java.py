import os

def collect_java_files_from_multimodule_project(project_path, output_file):
    """
    Собирает все файлы .java из всех модулей (app, data, domain и т.д.)
    в проекте Android Studio в один текстовый файл.

    Args:
        project_path (str): Путь к корневой папке проекта Android Studio.
        output_file (str): Имя файла, в который будут сохранены все .java файлы.
    """
    found_files = False
    with open(output_file, 'w', encoding='utf-8') as outfile:
        # Ищем модули в корневой папке проекта (например, 'app', 'data', 'domain')
        for module_name in os.listdir(project_path):
            module_path = os.path.join(project_path, module_name)
            
            # Проверяем, что это папка и может быть модулем
            if os.path.isdir(module_path):
                # Стандартный путь к исходникам в каждом модуле
                source_path = os.path.join(module_path, 'src', 'main', 'java')
                
                if os.path.exists(source_path) and os.path.isdir(source_path):
                    print(f"Найдены исходники в модуле: '{module_name}'...")
                    
                    # Рекурсивный обход всех поддиректорий в найденной папке исходников
                    for root, dirs, files in os.walk(source_path):
                        for file in files:
                            if file.endswith('.java'):
                                found_files = True
                                file_path = os.path.join(root, file)
                                relative_path = os.path.relpath(file_path, project_path)
                                
                                # Записываем заголовок с путем к файлу
                                outfile.write(f"// ========================================\n")
                                outfile.write(f"// Файл: {relative_path}\n")
                                outfile.write(f"// ========================================\n\n")
                                
                                # Записываем содержимое файла
                                try:
                                    with open(file_path, 'r', encoding='utf-8') as infile:
                                        outfile.write(infile.read())
                                        outfile.write('\n\n')
                                except Exception as e:
                                    outfile.write(f"// Не удалось прочитать файл: {relative_path}\n")
                                    outfile.write(f"// Ошибка: {e}\n\n")

    if found_files:
        print(f"\nВсе .java файлы были успешно собраны в файл '{output_file}'")
    else:
        print(f"В проекте по пути '{project_path}' не найдено ни одного .java файла в папках 'src/main/java'.")
        print("Пожалуйста, убедитесь, что путь к проекту указан верно.")


# --- ИНСТРУКЦИЯ ПО ИСПОЛЬЗОВАНИЮ ---
if __name__ == '__main__':
    # 1. Укажите полный путь к вашему проекту Android Studio
    #    Например: r'C:\Users\YourUser\AndroidStudioProjects\MyApplication' для Windows
    #    или '/home/user/AndroidStudioProjects/MyApplication' для Linux/macOS
    android_project_path = r'D:\4work\GitHub\MobileDevelopment\PocketOrnithology'

    # 2. Укажите имя файла, в который будут сохранены все .java файлы
    output_filename = 'collected_java_files_full.txt'

    # Проверка, был ли изменен путь к проекту
    if android_project_path == 'ПУТЬ_К_ВАШЕМУ_ПРОЕКТУ':
        print("Пожалуйста, укажите путь к вашему проекту Android Studio в переменной 'android_project_path'")
    else:
        collect_java_files_from_multimodule_project(android_project_path, output_filename)