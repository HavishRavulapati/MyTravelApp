cmake_minimum_required(VERSION 3.10.2)

project("MyTravelApp")

# Add the path to Faiss headers
include_directories(/opt/homebrew/opt/faiss/include)

# Add Faiss static library (if you're using static linking)
add_library(faiss STATIC /opt/homebrew/opt/faiss/lib/faiss.a)

# Or if you're using a shared library, use this instead:
# add_library(faiss SHARED /opt/homebrew/opt/faiss/lib/libfaiss.dylib)

# Link Faiss to your app's native C++ code
target_link_libraries(MyTravelApp PRIVATE faiss)
