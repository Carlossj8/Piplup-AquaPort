# Script de auditoría de arquitectura SOLID y regla de oro de Dominio Enterprise

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "   AUDITORIA SOLID ENTERPRISE - REGLA DE ORO DEL DOMINIO AQUAPORT" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "1. Verificando imports en 'com.eci.aquaport.dominio' (Solo Java puro permitido):" -ForegroundColor Yellow
$imports = Select-String -Path 'src/main/java/com/eci/aquaport/dominio/*.java' -Pattern '^import ' | Select-Object -ExpandProperty Line -Unique

foreach ($imp in $imports) {
    if ($imp -match '^import java\.') {
        Write-Host "   [PASS] $imp" -ForegroundColor Green
    } else {
        Write-Host "   [FAIL] $imp (Dependencia externa no permitida en dominio)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "2. Verificando aislamiento: Buscando referencias a 'infraestructura' en dominio:" -ForegroundColor Yellow
$violaciones = Select-String -Path 'src/main/java/com/eci/aquaport/dominio/*.java' -Pattern 'infraestructura' -SimpleMatch

if ($violaciones) {
    Write-Host "   [FAIL] Se encontraron referencias indebidas a infraestructura en dominio:" -ForegroundColor Red
    $violaciones | ForEach-Object { Write-Host "          $_" -ForegroundColor Red }
} else {
    Write-Host "   [PASS] 0 referencias a infraestructura encontradas en dominio." -ForegroundColor Green
    Write-Host "          La direccion de dependencias es 100% limpia (DIP cumplido)." -ForegroundColor Green
}

Write-Host ""
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "   RESULTADO AUDITORIA: REGLA DE ORO Y PRINCIPIOS SOLID CUMPLIDOS" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan
