import os

for fpathe, dirs, fs in os.walk('总结文档'):
    filenames = []
    for f in fs:
        if (f.endswith('.md')):
            print(os.path.join(fpathe, f))
            name = f.replace('.md','')
            filenames.append("* [%s](%s)\n" % (name, name))
            # * [git合并maven发布流程](git合并maven发布流程)
    if len(filenames) > 0:
        with open(os.path.join(fpathe, '_sidebar.md'), "w") as file:
            file.writelines(filenames)
        if os.path.exists(os.path.join(fpathe, 'README.md')) == False:
            with open(os.path.join(fpathe, 'README.md'), "w") as readme:
                readme.write(fpathe)
