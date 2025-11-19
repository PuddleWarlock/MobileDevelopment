import os
import re

def remove_java_style_comments(text):
    """
    Удаляет комментарии в стиле Java/C/Kotlin (// и /* ... */),
    игнорируя символы комментариев внутри строк.
    """
    def replacer(match):
        s = match.group(0)
        if s.startswith('/'):
            # Это комментарий — заменяем на пробел или пустую строку
            # (пробел безопаснее, чтобы не склеить код: int a;/*...*/int b;)
            return " " 
        else:
            # Это строка — возвращаем как есть
            return s

    # Регулярное выражение ищет:
    # 1. Строки в двойных кавычках (учитывая экранирование)
    # 2. Строки в одинарных кавычках
    # 3. Многострочные комментарии /* ... */
    # 4. Однострочные комментарии // ...
    pattern = re.compile(
        r'"(?:\\.|[^\\"])*"|\'(?:\\.|[^\\\'])*\'|/\*[\s\S]*?\*/|//.*?$',
        re.MULTILINE
    )
    
    return re.sub(pattern, replacer, text)

def remove_xml_comments(text):
    """
    Удаляет комментарии в стиле XML (<!-- ... -->).
    """
    # Ищем конструкцию <!-- любой текст -->
    # [\s\S] используется, чтобы точка захватывала и переносы строк
    return re.sub(r'<!--[\s\S]*?-->', '', text)

def clean_empty_lines(text):
    """
    Удаляет лишние пустые строки, оставшиеся после удаления комментариев.
    """
    lines = text.splitlines()
    cleaned_lines = [line for line in lines if line.strip()]
    return '\n'.join(cleaned_lines)

def clean_project_files(project_path):
    """
    Проходит по всем файлам проекта, удаляет комментарии из .java и .xml
    """
    # Папки, которые стоит игнорировать
    ignored_dirs = {'.git', '.gradle', '.idea', 'build', 'gradle'}
    
    files_processed = 0
    
    print(f"Начинаем очистку проекта: {project_path}")
    
    for root, dirs, files in os.walk(project_path):
        # Удаляем игнорируемые папки из списка обхода, чтобы не заходить в них
        dirs[:] = [d for d in dirs if d not in ignored_dirs]

        for file in files:
            file_path = os.path.join(root, file)
            ext = os.path.splitext(file)[1].lower()
            
            original_content = ""
            new_content = ""
            modified = False

            if ext == '.java':
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        original_content = f.read()
                    
                    # Удаляем комментарии
                    temp_content = remove_java_style_comments(original_content)
                    
                    # (Опционально) Удаляем пустые строки, если файл стал дырявым
                    # new_content = clean_empty_lines(temp_content) 
                    new_content = temp_content # Если хотите сохранить форматирование строк, используйте это

                    if new_content != original_content:
                        modified = True
                except Exception as e:
                    print(f"Ошибка чтения {file}: {e}")

            elif ext == '.xml':
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        original_content = f.read()
                    
                    new_content = remove_xml_comments(original_content)
                    
                    if new_content != original_content:
                        modified = True
                except Exception as e:
                    print(f"Ошибка чтения {file}: {e}")

            # Если были изменения, перезаписываем файл
            if modified:
                try:
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    print(f"Очищен: {os.path.relpath(file_path, project_path)}")
                    files_processed += 1
                except Exception as e:
                    print(f"Ошибка записи {file}: {e}")

    print(f"\nГотово. Обработано файлов: {files_processed}")

# --- ЗАПУСК ---
if __name__ == '__main__':
    # Укажите путь к проекту
    android_project_path = r'D:\4work\GitHub\MobileDevelopment\PocketOrnithology'
    
    # Защита от случайного запуска на пустом пути
    if os.path.exists(android_project_path):
        # Спрашиваем подтверждение
        confirm = input(f"ВЫ УВЕРЕНЫ? Это необратимо удалит комментарии из файлов в '{android_project_path}'. (y/n): ")
        if confirm.lower() == 'y':
            clean_project_files(android_project_path)
        else:
            print("Отменено.")
    else:
        print("Указанный путь не существует.")