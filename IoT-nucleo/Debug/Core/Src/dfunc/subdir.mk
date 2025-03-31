################################################################################
# Automatically-generated file. Do not edit!
# Toolchain: GNU Tools for STM32 (13.3.rel1)
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../Core/Src/dfunc/fdex.c 

OBJS += \
./Core/Src/dfunc/fdex.o 

C_DEPS += \
./Core/Src/dfunc/fdex.d 


# Each subdirectory must supply rules for building sources it contributes
Core/Src/dfunc/%.o Core/Src/dfunc/%.su Core/Src/dfunc/%.cyclo: ../Core/Src/dfunc/%.c Core/Src/dfunc/subdir.mk
	arm-none-eabi-gcc "$<" -mcpu=cortex-m0 -std=gnu11 -g3 -DDEBUG -DUSE_HAL_DRIVER -DSTM32F030x8 -c -I../Core/Inc -I../Drivers/STM32F0xx_HAL_Driver/Inc -I../Drivers/STM32F0xx_HAL_Driver/Inc/Legacy -I../Drivers/CMSIS/Device/ST/STM32F0xx/Include -I../Drivers/CMSIS/Include -O0 -ffunction-sections -fdata-sections -Wall -fstack-usage -fcyclomatic-complexity -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" --specs=nano.specs -mfloat-abi=soft -mthumb -o "$@"

clean: clean-Core-2f-Src-2f-dfunc

clean-Core-2f-Src-2f-dfunc:
	-$(RM) ./Core/Src/dfunc/fdex.cyclo ./Core/Src/dfunc/fdex.d ./Core/Src/dfunc/fdex.o ./Core/Src/dfunc/fdex.su

.PHONY: clean-Core-2f-Src-2f-dfunc

